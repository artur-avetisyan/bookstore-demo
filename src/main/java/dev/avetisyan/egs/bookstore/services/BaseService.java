package dev.avetisyan.egs.bookstore.services;

import org.modelmapper.ModelMapper;

public abstract class BaseService {

    protected final ModelMapper mapper;

    protected BaseService(ModelMapper mapper) {
        this.mapper = mapper;
    }
}
