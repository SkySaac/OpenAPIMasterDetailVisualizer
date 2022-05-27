package com.example.application.data.structureModel.paths;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Path {
    private String path;
    private PathMethod get; //TODO
    private PathMethod put; //TODO
    private PathMethod post; //TODO
    private PathMethod delete; //TODO
}
