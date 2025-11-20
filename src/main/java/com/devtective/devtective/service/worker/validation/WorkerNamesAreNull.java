package com.devtective.devtective.service.worker.validation;

import com.devtective.devtective.dominio.worker.Worker;

public class WorkerNamesAreNull implements WorkerValidationRule {

    @Override
    public void validate(Worker worker) {
        if (worker.getFirstName() == null) {
            worker.setFirstName("");
        } 
        if (worker.getLastName() == null) {
            worker.setLastName("");
        }
    }
    
}
