CREATE TABLE test."Customer" (
  name varchar(50) NOT NULL,
  vorname varchar(50),
  rabattstufe int8 NOT NULL DEFAULT 0,
  guthaben money,
  id int8 NOT NULL DEFAULT nextval('test."Customer_id_seq"'::text),
  CONSTRAINT pk_customer PRIMARY KEY (id)
);

CREATE TABLE test."Product" (
  id int4 NOT NULL DEFAULT nextval('test."Product_id_seq"'::text),
  name varchar(50) NOT NULL,
  quantity int8 NOT NULL DEFAULT 0,
  CONSTRAINT pk_product PRIMARY KEY (id)
);

CREATE TABLE test."Order" (
  id int4 NOT NULL DEFAULT nextval('test."Order_id_seq"'::text),
  datum date NOT NULL,
  customer_id int8 NOT NULL,
  CONSTRAINT pk_order PRIMARY KEY (id),
  CONSTRAINT fk_customer FOREIGN KEY (customer_id) REFERENCES test."Customer" (id) ON UPDATE RESTRICT ON DELETE RESTRICT
);

CREATE TABLE test."OrderLine" (
  product_id int8 NOT NULL,
  quantity int8 NOT NULL,
  order_id int8 NOT NULL,
  CONSTRAINT pk_orderline PRIMARY KEY (order_id, product_id),
  CONSTRAINT fk_order FOREIGN KEY (order_id) REFERENCES test."Order" (id) ON UPDATE RESTRICT ON DELETE RESTRICT,
  CONSTRAINT fk_product FOREIGN KEY (product_id) REFERENCES test."Product" (id) ON UPDATE RESTRICT ON DELETE RESTRICT
);
