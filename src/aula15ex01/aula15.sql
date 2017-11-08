DROP TABLE produto;

CREATE TABLE produto(
nome VARCHAR (80),
qtd INTEGER,
atualizado TIMESTAMP
);

INSERT INTO produto (nome, qtd, atualizado)
VALUES('Produto X', 10, CURRENT_TIMESTAMP);