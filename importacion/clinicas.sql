-- Ejecutar en TiDB SQL Editor. Conserva las clinicas que ya tengan estos IDs.
USE vitalink;

INSERT INTO clinics (id, name)
SELECT 'CL-01', 'Clínica Central'
WHERE NOT EXISTS (SELECT 1 FROM clinics WHERE id = 'CL-01');

INSERT INTO clinics (id, name)
SELECT 'CL-02', 'Clínica San José'
WHERE NOT EXISTS (SELECT 1 FROM clinics WHERE id = 'CL-02');

INSERT INTO clinics (id, name)
SELECT 'CL-03', 'Clínica Vida'
WHERE NOT EXISTS (SELECT 1 FROM clinics WHERE id = 'CL-03');

SELECT id, name FROM clinics ORDER BY id;
