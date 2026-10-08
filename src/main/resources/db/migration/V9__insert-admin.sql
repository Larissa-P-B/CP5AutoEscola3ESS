INSERT INTO usuarios (login, senha, perfil)
SELECT
    'admin',
    '$2a$10$ekulOHXSWt8D6ylDwLZLe.3./Adw5Z5844AELnA0jBAjltGPe.bgS',
    'ADMIN'
WHERE NOT EXISTS (
    SELECT 1
    FROM usuarios
    WHERE login = 'admin'
);