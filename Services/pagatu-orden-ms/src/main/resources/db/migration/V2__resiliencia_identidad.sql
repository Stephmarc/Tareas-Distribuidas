-- Preserve V1 and existing records; never reset the student's database.
ALTER TABLE ordenes ALTER COLUMN cliente_id TYPE VARCHAR(100) USING cliente_id::text;
ALTER TABLE ordenes ALTER COLUMN total DROP NOT NULL;
ALTER TABLE detalles_orden ALTER COLUMN precio_unitario DROP NOT NULL;
ALTER TABLE detalles_orden ADD COLUMN nombre_producto VARCHAR(255);
