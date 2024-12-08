package com.luxoft.bankapp.service.storage;

import com.luxoft.bankapp.model.Client;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface ClientRepository {

    Client add(Client client);

    Client get(long id);

    Client getBy(String name);

    List<Client> getAll();

    Client update(Client o);

    boolean remove(long id);
}
