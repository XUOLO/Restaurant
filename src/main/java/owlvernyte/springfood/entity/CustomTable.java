package owlvernyte.springfood.entity;

import jakarta.persistence.*;

public class CustomTable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;
    private String status;
    private String description;
}
