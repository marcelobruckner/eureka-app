INSERT INTO aluno (nome, email, usuario, senha, excluido)
SELECT 'Livia', 'livia@email.com', 'livia', '$2a$10$s54VaYac6QTI9Y/ZVFN5d.IHCzW7lB4k/nkHs0T2CFrxrnv38av5m', FALSE
WHERE NOT EXISTS (
    SELECT 1
    FROM aluno
    WHERE usuario = 'livia'
);

INSERT INTO ano_letivo (ano, aluno_id, excluido)
SELECT 2026, a.id, FALSE
FROM aluno a
WHERE a.usuario = 'livia'
  AND NOT EXISTS (
      SELECT 1
      FROM ano_letivo al
      WHERE al.aluno_id = a.id
        AND al.ano = 2026
        AND al.excluido = FALSE
  );

INSERT INTO disciplina (nome, ano_letivo_id, excluido)
SELECT 'Português', al.id, FALSE
FROM ano_letivo al
JOIN aluno a ON a.id = al.aluno_id
WHERE a.usuario = 'livia'
  AND al.ano = 2026
  AND NOT EXISTS (
      SELECT 1
      FROM disciplina d
      WHERE d.ano_letivo_id = al.id
        AND d.nome = 'Português'
        AND d.excluido = FALSE
  );

INSERT INTO disciplina (nome, ano_letivo_id, excluido)
SELECT 'Matemática', al.id, FALSE
FROM ano_letivo al
JOIN aluno a ON a.id = al.aluno_id
WHERE a.usuario = 'livia'
  AND al.ano = 2026
  AND NOT EXISTS (
      SELECT 1
      FROM disciplina d
      WHERE d.ano_letivo_id = al.id
        AND d.nome = 'Matemática'
        AND d.excluido = FALSE
  );

INSERT INTO disciplina (nome, ano_letivo_id, excluido)
SELECT 'Ciências', al.id, FALSE
FROM ano_letivo al
JOIN aluno a ON a.id = al.aluno_id
WHERE a.usuario = 'livia'
  AND al.ano = 2026
  AND NOT EXISTS (
      SELECT 1
      FROM disciplina d
      WHERE d.ano_letivo_id = al.id
        AND d.nome = 'Ciências'
        AND d.excluido = FALSE
  );
