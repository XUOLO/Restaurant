package owlvernyte.springfood.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import owlvernyte.springfood.entity.Contact;
import owlvernyte.springfood.repository.ContactRepository;

import java.util.List;

@Service
public class ContactService {

    @Autowired
    private ContactRepository contactRepository;

    List<Contact> getAllContact(){ return  contactRepository.findAll();}



    public void saveContact(Contact contact) {
        this.contactRepository.save(contact);

    }

}
