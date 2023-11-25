package ru.mai.trainticketsalesapp.mapper;

import java.util.List;

public interface Mappable<E, D> {

    E toEntity(D d);

    D toDTO(E e);

    List<D> toDTO(List<E> e);

}
