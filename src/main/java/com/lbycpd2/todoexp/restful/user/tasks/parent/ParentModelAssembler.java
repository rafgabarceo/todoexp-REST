package com.lbycpd2.todoexp.restful.user.tasks.parent;

import com.lbycpd2.todoexp.restful.user.UserController;
import lombok.SneakyThrows;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class ParentModelAssembler implements RepresentationModelAssembler<ParentTask, EntityModel<ParentTask>> {
    @SneakyThrows
    @Override
    public EntityModel<ParentTask> toModel(ParentTask entity) {
        return EntityModel.of(entity,
                linkTo(methodOn(UserController.class).getUserParentTasks(entity.getParentId())).withSelfRel(),
                linkTo(methodOn(UserController.class).getParentTask(entity.getParentId())).withRel("ptasks"));
    }

    @Override
    public CollectionModel<EntityModel<ParentTask>> toCollectionModel(Iterable<? extends ParentTask> entities) {
        return null;
    }
}
