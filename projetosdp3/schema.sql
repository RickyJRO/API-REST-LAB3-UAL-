DROP TABLE IF EXISTS entregas;
DROP TABLE IF EXISTS stock;

CREATE TABLE entregas
(
    id int,
    nomeproduto varchar(255),
    quantidade bigint,
    locall varchar(255)
);
CREATE TABLE stock
(
    id SERIAL,
    nome varchar(255) NOT NULL,
    quantidade int DEFAULT 0,
    descricao varchar(255) DEFAULT 'Sem descricao',
    CONSTRAINT "Stock_pkey" PRIMARY KEY (id)
);
