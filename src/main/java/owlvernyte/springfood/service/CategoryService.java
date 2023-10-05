package owlvernyte.springfood.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import owlvernyte.springfood.entity.Category;
import owlvernyte.springfood.repository.CategoryRepository;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {
    @Autowired
    private CategoryRepository categoryRepository;

    public List<Category> getAllCategory() {
        return categoryRepository.findAll();
    }

    public void saveCategory(Category category) {
        this.categoryRepository.save(category);

    }
    public Category viewById(long id) {
        return categoryRepository.findById(id).get();
    }

    public Category getCategoryById(long id) {
        Optional<Category> optional = categoryRepository.findById(id);
        Category category = null;
        if (optional.isPresent()) {
            category = optional.get();
        }
        else
        {
            throw new RuntimeException(" Cant find product id : " + id);
        }
        return category;
    }

    public void deleteCategoryById(long id) {
        this.categoryRepository.deleteById(id);
    }
}
