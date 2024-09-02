package com.gmail.deniska1406sme;


import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import java.util.List;

public class App {
    static EntityManagerFactory emf = Persistence.createEntityManagerFactory("JPATest");
    static EntityManager em = emf.createEntityManager();


    public static void main( String[] args ) {

        createClient("Denys","main st 1","38091");
        createClient("Olga","main st 2","38092");
        createClient("Victor","main st 3","38093");

        createProduct("iPhone 12", 1000.00,10);
        createProduct("Samsung s20", 1200.00,10);
        createProduct("Xiaomi 15", 820.00,10);

        createOrder(1L,4L,7);
        createOrder(2L,5L,7);
        createOrder(3L,6L,4);

        viewObject(Order.class);

        deleteOrder(8L);

        viewObject(Order.class);

    }

    public static void createClient(String name,String address,String phone){
        em.getTransaction().begin();
        try {
            Client client = new Client(name,address,phone);
            em.persist(client);
            em.getTransaction().commit();
        }catch(Exception e){
            em.getTransaction().rollback();
            e.printStackTrace();
        }
    }
    public static void createProduct(String name,double price,int quantity){
        em.getTransaction().begin();
        try {
            Product product = new Product(name, price, quantity);
            em.persist(product);
            em.getTransaction().commit();
        }catch(Exception e){
            em.getTransaction().rollback();
            e.printStackTrace();
        }
    }

    public static void createOrder(Long clientId,Long productId,Integer quantity) {
        em.getTransaction().begin();
        try {
            Client client = em.find(Client.class, clientId);
            Product product = em.find(Product.class, productId);
            checkQuantity(product,quantity);

            Order order = new Order(quantity);
            order.setClient(client);
            order.setProduct(product);
            product.setQuantity(product.getQuantity() - quantity);
            order.setTotalPrice(product.getPrice() * quantity);
            em.persist(order);
            em.getTransaction().commit();

        }catch (Exception e){
            em.getTransaction().rollback();
            e.printStackTrace();
        }
    }

    public static void deleteOrder(Long id) {
        Order order = em.getReference(Order.class, id);
        em.getTransaction().begin();
        try {
            order.getProduct().setQuantity(order.getProduct().getQuantity() + order.getQuantity());
            em.remove(order);
            em.getTransaction().commit();
        }catch (Exception e){
            em.getTransaction().rollback();
            e.printStackTrace();
        }

    }


    public static <T> void viewObject(Class<T> clazz) {
        String className = clazz.getSimpleName();
        Query query = em.createQuery("select o from " + className + " o" ,clazz);
        List<T> arr = query.getResultList();

        for (T obj : arr) {
            System.out.println(obj);
        }
    }

    public static void checkQuantity(Product product, Integer quantity) throws IllegalAccessException {
        if (product.getQuantity() < quantity) {
            throw new IllegalAccessException("Only " + product.getQuantity() +" items left");
        }
    }
}
