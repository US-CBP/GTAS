INSERT INTO gtas.graph_rules (id, created_at, created_by, updated_at, updated_by, cipherQuery)
VALUES (1, null, null, null, null, 'match (p:Passenger) where p.id_tag in $id_tag return p.id_tag;');