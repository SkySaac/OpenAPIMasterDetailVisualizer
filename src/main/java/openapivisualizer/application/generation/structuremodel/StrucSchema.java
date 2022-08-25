package openapivisualizer.application.generation.structuremodel;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StrucSchema {
    private String name;
    private StrucValue strucValue;
    private boolean isFreeSchema = false; //TODO remove

}
