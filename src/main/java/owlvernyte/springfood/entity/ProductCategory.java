package owlvernyte.springfood.entity;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import java.util.List;

@Entity
@Table(name = "ProductCategory")
public class ProductCategory {

	  @Id
	  @GeneratedValue(strategy = GenerationType.IDENTITY)
	  private Long id;

	@NotBlank(message = "Đặc điểm không được để trống")
	@Column(name = "Description")
	private String description;
	  @Column(name = "Name")
	  private String name;
	@OneToMany(mappedBy = "productCategory", cascade =  CascadeType.ALL)
	private List<Product> product;


	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public ProductCategory() {
	}


	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Product> getProduct() {
		return product;
	}

	public void setProduct(List<Product> product) {
		this.product = product;
	}
}
