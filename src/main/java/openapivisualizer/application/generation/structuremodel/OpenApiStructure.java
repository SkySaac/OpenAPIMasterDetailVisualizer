package openapivisualizer.application.generation.structuremodel;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class OpenApiStructure {

    private boolean hasHttpBasic = false;

    private List<String> servers = new ArrayList<>();

    private List<ViewGroup> viewGroups = new ArrayList<>();
}
