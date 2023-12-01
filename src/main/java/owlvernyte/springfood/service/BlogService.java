package owlvernyte.springfood.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import owlvernyte.springfood.entity.Blog;
import owlvernyte.springfood.entity.Contact;
import owlvernyte.springfood.entity.Product;
import owlvernyte.springfood.repository.BlogRepository;

import java.util.List;
import java.util.Optional;

@Service
public class BlogService {
    @Autowired
    private BlogRepository blogRepository;

    public List<Blog> getAllBlog(){ return  blogRepository.findAll();}



    public Blog viewById(long id) {
        return blogRepository.findById(id).get();
    }
    public void saveBlog(Blog blog) {
        this.blogRepository.save(blog);
    }
    public Blog getBlogById(long id) {
        Optional<Blog> optional = blogRepository.findById(id);
        Blog blog = null;
        if (optional.isPresent()) {
            blog = optional.get();
        }
        else
        {
            throw new RuntimeException(" Cant find blog id : " + id);
        }
        return blog;
    }
    public void deleteBlogById(long id) {
        this.blogRepository.deleteById(id);
    }


    public Page<Blog> findPaginatedProduct(int pageNo, int pageSize,String sortField,String sortDirection){
        Sort sort= sortDirection.equalsIgnoreCase(Sort.Direction.ASC.name())? Sort.by(sortField).ascending():
                Sort.by(sortField).descending();


        Pageable pageable= PageRequest.of(pageNo - 1,pageSize,sort);
        return this.blogRepository.findAll(pageable);
    }



    public List<Blog> searchBlogAdmin(String keyword) {

        if(keyword!=null){
            return blogRepository.findAll(keyword);
        }
        return blogRepository.findAll();
    }


}
