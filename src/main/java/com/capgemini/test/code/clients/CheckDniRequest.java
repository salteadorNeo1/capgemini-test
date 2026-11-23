package com.capgemini.test.code.clients;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CheckDniRequest {
  private String dni;

    public CheckDniRequest(String dni) {
        this.dni = dni;
    }
}
