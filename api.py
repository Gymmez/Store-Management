import datetime
from flask import Flask, render_template, request, redirect, jsonify
from flask_sqlalchemy import SQLAlchemy

app = Flask(__name__)

app.config['SQLALCHEMY_DATABASE_URI'] = 'mysql+pymysql://wsltest:Test1234!@172.29.64.1:3306/javadb'
app.config['SQLALCHEMY_TRACK_MODIFICATIONS'] = False

db = SQLAlchemy(app)

class Customer(db.Model):
    __tablename__ = 'customer'
    id = db.Column('CustomerID', db.Integer, primary_key=True, autoincrement=True)
    name = db.Column(db.String(100), nullable=False)
    address = db.Column(db.String(100), nullable=False)
    orders = db.relationship("Orders", backref="customer", cascade="all, delete-orphan")
    def to_dict(self):
        return {"id":self.id,"name":self.name,"address":self.address}

class Orders(db.Model):
    __tablename__ = 'orders'
    id = db.Column('orderID', db.Integer, primary_key=True, autoincrement=True)

    price = db.Column('orderprice', db.Numeric(precision=8, scale=2), nullable=False)

    ordercustomerID = db.Column(
        "ordercustomerID",
        db.Integer,
        db.ForeignKey("customer.CustomerID"),
        nullable=False
    )

    items = db.relationship(
        "OrderItem",
        backref="order",
        cascade="all, delete-orphan"
    )

    payments = db.relationship(
        "Payment",
        backref="order",
        cascade="all, delete-orphan"
    )

    def to_dict(self):
        return {
            "id": self.id,
            "price": float(self.price),
            "ordercustomerID": self.ordercustomerID
        }



class OrderItem(db.Model):
    __tablename__ = 'orderitem'
    id = db.Column('orderitemID', db.Integer, primary_key=True, autoincrement=True)
    name = db.Column("Name", db.String(100), nullable=False)
    price = db.Column('itemprice', db.Numeric(precision=8, scale=2), nullable=False)
    orderitemorderID = db.Column("orderitemorderID", db.Integer, db.ForeignKey("orders.orderID"), nullable=False)
    def to_dict(self):
        return {"id":self.id,"name":self.name,"price":self.price,"orderitemorderID":self.orderitemorderID}


class Payment(db.Model):
    __tablename__ = 'payment'
    id = db.Column('paymentID', db.Integer, primary_key=True, autoincrement=True)

    amount = db.Column('amount', db.Numeric(precision=8, scale=2), nullable=False)
    paydate = db.Column('paymentDate', db.DateTime, default=datetime.datetime.utcnow)
    holder = db.Column("HolderName", db.String(100), nullable=True)
    cardnumber = db.Column("CardNumber", db.String(100), nullable=True)

    paymentorderID = db.Column(
        "paymentorderID",
        db.Integer,
        db.ForeignKey("orders.orderID"),
        nullable=False
    )

    def to_dict(self):
        return {
            "id": self.id,
            "amount": float(self.amount),
            "paydate": self.paydate,
            "holder": self.holder,
            "cardnumber": self.cardnumber,
            "paymentorderID": self.paymentorderID
        }


## API ENDPOINTS

@app.route('/', methods=["GET"])
def home():
    return render_template('index.html')

@app.route('/customers', methods=['GET','POST'])
def customer():
    if request.method == 'POST':
        name=request.form.get('cname')
        address=request.form.get('caddress')
        add=Customer(name=name, address=address)
        
        try:
            
            db.session.add(add)
            db.session.commit()
            return redirect('/customers') 
        except Exception as e:
            return f"There was an issue adding the customer: {e}"
    
    s=Customer.query.all()
    return render_template('customer.html',users=s)


@app.route('/orders', methods=['GET'])
def orders():
    
    all_orders = Orders.query.all()
    return render_template('order.html', users=all_orders)



@app.route('/payments', methods=['GET', 'POST'])
def payments():
    if request.method == 'POST':
        amount = request.form.get('amount')
        holder = request.form.get('holder')
        card = request.form.get('card_number')
        
        new_payment = Payment(amount=amount, holder=holder, cardnumber=card)
        try:
            db.session.add(new_payment)
            db.session.commit()
            return redirect('/payments')
        except Exception as e:
            db.session.rollback()
            return f"Error processing payment: {e}"

    all_payments = Payment.query.all()
    return render_template('payment.html', payments=all_payments)


@app.route('/order/<int:orderid>/getorder')
def getorder(orderid):
    order=Orders.query.get(orderid)
    if order is None:
        return jsonify({"error":"id is not found!"}),404
    return jsonify(order.to_dict())
@app.route('/order/postorder', methods=['POST'])
def postorder():
    data = request.get_json(force=True, silent=False)
    if not data:
        return jsonify({"error": "Invalid or missing JSON"}), 400

    if "ordercustomerID" not in data or "orderitems" not in data:
        return jsonify({"error": "some fields are missing"}), 400

    orderitems = data["orderitems"]

    order = Orders(ordercustomerID=data["ordercustomerID"], price=0)
    db.session.add(order)
    db.session.commit()

    for item in orderitems:
        orderitem = OrderItem(
            name=item["name"],
            price=item["price"],
            orderitemorderID=order.id
        )
        db.session.add(orderitem)
        order.price += item["price"]

    db.session.commit()
    return jsonify(order.to_dict())

@app.route("/customer/<int:customerid>/getcustomer")
def getcustomer(customerid):
    customer=Customer.query.get(customerid)
    if customer is None:
        return jsonify({"error":"id not found"}),404
    return jsonify(customer.to_dict())
@app.route('/payment/postpayment', methods=['POST'])
def postpayment():
    data = request.get_json(force=True, silent=False)

    if not data:
        return jsonify({"error": "Invalid or missing JSON"}), 400

    required_fields = ["paymentorderID", "amount"]
    for field in required_fields:
        if field not in data:
            return jsonify({"error": f"{field} is required"}), 400

    order = Orders.query.get(data["paymentorderID"])
    if not order:
        return jsonify({"error": "Order does not exist"}), 400

    payment = Payment(
        amount=data["amount"],
        holder=data.get("holder"),
        cardnumber=data.get("cardnumber"),
        paymentorderID=order.id
    )

    try:
        db.session.add(payment)
        db.session.commit()
    except Exception as e:
        db.session.rollback()
        return jsonify({"error": str(e)}), 500

    return jsonify(payment.to_dict()), 201

if __name__ == "__main__":
    app.run(port=8080,debug=True)
   