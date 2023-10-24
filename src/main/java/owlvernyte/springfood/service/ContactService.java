package owlvernyte.springfood.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import owlvernyte.springfood.entity.Contact;
import owlvernyte.springfood.entity.Product;
import owlvernyte.springfood.repository.ContactRepository;

import java.util.List;

@Service
public class ContactService {

    @Autowired
    private ContactRepository contactRepository;

    public List<Contact> getAllContact(){ return  contactRepository.findAll();}


    public List<Contact> searchContactAdmin(String keyword) {

        if(keyword!=null){
            return contactRepository.findAll(keyword);
        }
        return contactRepository.findAll();
    }
    public void saveContact(Contact contact) {
        this.contactRepository.save(contact);

    }

}
