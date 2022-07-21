package com.example.application.ui.components;

import com.example.application.data.structureModel.StrucPath;
import com.example.application.data.structureModel.StrucSchema;

public class PutDialog extends PostDialog {
    public PutDialog(PostActionListener actionListener) {
        super(actionListener);
    }


    public void open(StrucSchema schema, StrucPath strucPath) { //TODO only needs strucpath since the schema is in there
        //Prepare Dialog just as POST
        super.open(schema, strucPath);

    }
}
