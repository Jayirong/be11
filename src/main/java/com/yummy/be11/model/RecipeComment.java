package com.yummy.be11.model;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "recipe_comments")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecipeComment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_recipe_com;  

    //rel
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_user", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_recipe", nullable = false)
    private Recipe recipe;
    //endrel

    @Column(nullable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private LocalDateTime fecha_creacion_com;

    @Column(columnDefinition = "CLOB", nullable = false)
    private String comentario;

    @Column(nullable = false)
    private Boolean estado;

    //init fecha
    @PrePersist
    protected void onCreate() {
        this.fecha_creacion_com = LocalDateTime.now();
    }
    
}
