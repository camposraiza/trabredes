

CREATE TABLE cliente(
nome VARCHAR (80),
senha VARCHAR (20)
);

INSERT INTO cliente (nome, senha)
VALUES('Raiza', '123456');

CREATE TABLE ticket(
idCliente INTEGER,
numero VARCHAR (80),
ticket VARCHAR (80)
);

