package ru.netology.repository;

import org.springframework.stereotype.Repository;
import ru.netology.exception.NotFoundException;
import ru.netology.model.Post;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Repository
public class PostRepositoryStubImpl implements PostRepository {
    //этот метод не потокобезопасен,
    // так как использует инкрементированиие этой переменной, которая неатомарна
    //привела решение в другой задаче - если оно верное, перенесу в этот код
    private int count;
    private ConcurrentMap<Long, Post> collectionRequests;

    public PostRepositoryStubImpl() {
        count = 0;
        collectionRequests = new ConcurrentHashMap<>();
    }

    public List<Post> all() {
        List<Post> listAll = new ArrayList<>();
        for (Post postR : collectionRequests.values()) {
            listAll.add(postR);
        }
        return listAll;
    }

    public Optional<Post> getById(long id) {
        if (collectionRequests.containsKey(id)) {
            return Optional.of(collectionRequests.get(id));
        } else
            return Optional.empty();
    }

    public Post save(Post post) {
        Long idReques = post.getId();
        if (idReques == 0) {
            ++count;
            long newId = count;
            while (collectionRequests.containsKey(newId)) {
                ++count;
                newId = count;
            }
            post.setId(newId);
            collectionRequests.put(newId, post);
        } else {
            if (collectionRequests.containsKey(idReques)) {
                collectionRequests.replace(idReques, post);
            } else {
                collectionRequests.put(idReques, post);
            }
        }
        return post;
    }

    public void removeById(long id) {
        if (collectionRequests.containsKey(id)) {
            collectionRequests.remove(id);
            count--;
        } else {
            throw new NotFoundException();
        }
    }
}