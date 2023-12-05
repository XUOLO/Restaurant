package owlvernyte.springfood.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

import java.sql.Blob;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "Product")
public class Product {
	
	  @Id
	  @GeneratedValue(strategy = GenerationType.IDENTITY)
	  private Long id;


	@NotBlank(message = "Name not null")
	  @Column(name = "Name")
	  private String name;
	@NotBlank(message = "Recipe not null")
	@Column(name = "Recipe", columnDefinition = "LONGTEXT")
	private String recipe;

	@NotBlank(message = "Video recipe not null")
	@Column(name = "VideoRecipe")
	private String videoRecipe;
	@NotBlank(message = "Description not null ")
	  @Column(name = "Description")
	  private String description;

	  @Lob
	  private Blob image;

	@Positive(message = "Price > 0")
	  @Column(name = "Price")
	  private int price;
	  @NotBlank(message = "Detail not null")
	  @Column(name = "Detail")
	  private String detail;

	@Column(name = "Quantity")
	private int quantity;

	@OneToMany(mappedBy = "product")
	private List<Comment> comments;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "productCategory_id")
	private ProductCategory productCategory;

	public String getVideoRecipe() {
		return videoRecipe;
	}

	public void setVideoRecipe(String videoRecipe) {
		this.videoRecipe = videoRecipe;
	}

	public String getRecipe() {
		return recipe;
	}

	public void setRecipe(String recipe) {
		this.recipe = recipe;
	}

	public List<Comment> getComments() {
		return comments;
	}

	public void setComments(List<Comment> comments) {
		this.comments = comments;
	}

	public List<Rating> getRatings() {
		return ratings;
	}

	public void setRatings(List<Rating> ratings) {
		this.ratings = ratings;
	}

	public List<Order> getOrders() {
		return orders;
	}

	public void setOrders(List<Order> orders) {
		this.orders = orders;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	private LocalDateTime createTime;

	public LocalDateTime getCreateTime() {
		return createTime;
	}

	public void setCreateTime(LocalDateTime createTime) {
		this.createTime = createTime;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	public String getStatusString() {
		switch (status) {
			case "1":
				return "Selling";
			case "2":
				return "Not selling";

			default:
				return "Unknown";
		}
	}
	@OneToMany(mappedBy = "product")
	private List<Rating> ratings;
	@ManyToMany(mappedBy = "products")
	private List<Order> orders;

	public String getStatus() {
		return status;
	}

	private String status;

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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Blob getImage() {
		return image;
	}

	public void setImage(Blob image) {
		this.image = image;
	}

	public int getPrice() {
		return price;
	}

	public void setPrice(int price) {
		this.price = price;
	}

	public String getDetail() {
		return detail;
	}

	public void setDetail(String detail) {
		this.detail = detail;
	}

	public ProductCategory getProductCategory() {
		return productCategory;
	}

	public void setProductCategory(ProductCategory productCategory) {
		this.productCategory = productCategory;
	}

//	public Set<Reservation> getReservations() {
//		return reservations;
//	}
//
//	public void setReservations(Set<Reservation> reservations) {
//		this.reservations = reservations;
//	}
}
