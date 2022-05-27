package com.example.application.data.structureModel.finalStructure;

import com.example.application.data.structureModel.tags.Tag;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Component {
    private Tag tag;
    private Schema schema;

    public Component(Tag tag,Schema schema){
        this.tag = tag;
        this.schema = schema;
    }
}
