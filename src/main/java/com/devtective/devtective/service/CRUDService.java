package com.devtective.devtective.service;

public interface CRUDService<RQ, RS, ID> {

    RS create(RQ request);

    RS update(ID id, RQ request);

    RS getOne(ID id);

    void delete(ID id);
} 
