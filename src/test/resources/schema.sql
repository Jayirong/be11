CREATE TABLE recipe (
    id_recipe BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL,
    fecha_creacion TIMESTAMP NOT NULL,
    descripcion CLOB NOT NULL,
    tipo_cocina BIGINT NOT NULL,
    pais_origen BIGINT NOT NULL,
    dificultad BIGINT NOT NULL,
    img_ruta VARCHAR(255),
    id_user BIGINT NOT NULL
);