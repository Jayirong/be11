package com.yummy.be11.model;

import java.time.LocalDateTime;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "recipe")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Recipe {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_recipe;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    //formateo para consumir mediante la api en formato json
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private LocalDateTime fecha_creacion;

    @Column(columnDefinition = "CLOB", nullable = false)
    private String descripcion;

    @Column(nullable = false)
    private Long tipo_cocina;

    @Column(nullable = false)
    private Long pais_origen;

    @Column(nullable = false)
    private Long dificultad;

    @Column(name = "img_ruta")
    private String img_ruta;

    @Column(name = "id_user", nullable = false)
    private Long idUser;

    //rel
    @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<RecipeComment> comments;


     //init fecha
    @PrePersist
    protected void onCreate() {
        this.fecha_creacion = LocalDateTime.now();
    }

}
