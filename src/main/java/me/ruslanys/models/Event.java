package me.ruslanys.models;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author Ruslan Molchanov (ruslanys@gmail.com)
 */
@Data
@AllArgsConstructor
public class Event {

    private Long taskId;
    private Type type;
    private Status status;

    public enum Status {
        STARTED, SUCCESSFUL, FAILED
    }

}
