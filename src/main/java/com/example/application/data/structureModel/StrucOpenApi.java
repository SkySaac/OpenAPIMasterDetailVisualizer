package com.example.application.data.structureModel;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class StrucOpenApi {

    private List<String> servers = new ArrayList<>();

    private List<StrucViewGroup> strucViewGroups = new ArrayList<>();
}
