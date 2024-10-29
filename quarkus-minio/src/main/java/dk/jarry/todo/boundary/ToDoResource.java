package dk.jarry.todo.boundary;

import java.io.IOException;
import java.security.GeneralSecurityException;

import dk.jarry.todo.entity.ToDo;
import io.minio.errors.MinioException;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;

@Path("/todos")
public class ToDoResource {

    @Inject
	ToDoService toDoService;

	@POST
	public ToDo create(ToDo toDo) {
        try {
            toDoService.addObject(toDo.fileName());
        } catch (IOException | MinioException | GeneralSecurityException e) {
            e.printStackTrace();
        }
		return toDo;
	}

	@GET
	@Path("{fileName}")
	public ToDo read(@PathParam("fileName")  String fileName) {
        toDoService.getObject(fileName);
		return new ToDo(3, fileName);
	}

}
