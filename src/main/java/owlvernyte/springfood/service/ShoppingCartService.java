package owlvernyte.springfood.service;


import org.springframework.stereotype.Service;
import owlvernyte.springfood.entity.CartItem;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Service
public class ShoppingCartService {

    Map<Long, CartItem> shoppingCart = new HashMap<>();

    public void add(CartItem newItem) {
        CartItem cartItem = shoppingCart.get(newItem.getProductId());
        if (cartItem == null) {
            newItem.setQuantity(1); // Đặt giá trị mặc định là 1
            shoppingCart.put(newItem.getProductId(), newItem);
        } else {
            cartItem.setQuantity(cartItem.getQuantity() + 1);
        }
    }


    public void remove(long id){

        shoppingCart.remove(id);
    }


    public CartItem update(long productId, int quantity){
        CartItem cartItem = shoppingCart.get(productId);
        if (cartItem != null) {
            cartItem.setQuantity(quantity);
        }
        return cartItem;
    }

    public void clear(){

        shoppingCart.clear();
    }

    public Collection<CartItem> getAllCartItem(){

        return shoppingCart.values();
    }

    public int getCount(){

        return shoppingCart.values().size();
    }

    public  double getAmount(){

        return shoppingCart.values().stream()
                .mapToDouble(item->item.getQuantity()*item.getPrice()).sum();
    }
}
