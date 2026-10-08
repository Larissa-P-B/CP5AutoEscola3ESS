ALTER TABLE instrucoes
ADD COLUMN status VARCHAR(20) NOT NULL DEFAULT 'AGENDADA';

ALTER TABLE instrucoes
ADD COLUMN motivo_cancelamento VARCHAR(30);

ALTER TABLE instrucoes
ADD COLUMN data_cancelamento DATETIME;