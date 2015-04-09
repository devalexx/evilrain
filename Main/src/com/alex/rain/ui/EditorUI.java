/*******************************************************************************
 * Copyright 2013 See AUTHORS file.
 *
 * Licensed under the GNU GENERAL PUBLIC LICENSE V3
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.gnu.org/licenses/gpl.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package com.alex.rain.ui;

import com.alex.rain.managers.EditorManager;
import com.alex.rain.managers.ResourceManager;
import com.alex.rain.models.*;
import com.alex.rain.stages.EditableGameWorld;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import java.text.DecimalFormat;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EditorUI extends Table {
    private Skin skin;
    private EditableGameWorld stage;
    private EditorManager editorManager;
    private TextField posTextField, sizeTextField, nameTextField, angleTextField;
    private Label cursorPosValueLabel;
    private DecimalFormat floatFormat = new DecimalFormat("0.##");

    public EditorUI(EditableGameWorld stage, EditorManager editorManager) {
        setFillParent(true);
        this.stage = stage;
        skin = ResourceManager.getSkin();
        this.editorManager = editorManager;

        Table tableLeft = createMenuTable();
        Table tableRight = createPropertiesTable();
        Table tableBottom = createConsoleTable();

        Table topPanes = new Table();
        Table emptyTableLeft = new Table();
        Table emptyTableRight = new Table();
        final SplitPane leftSplitPane = new SplitPane(tableLeft, emptyTableLeft, false, skin.get("default-horizontal", SplitPane.SplitPaneStyle.class));
        leftSplitPane.setSplitAmount(0.5f);
        final SplitPane rightSplitPane = new SplitPane(emptyTableRight, tableRight, false, skin.get("default-horizontal", SplitPane.SplitPaneStyle.class));
        rightSplitPane.setSplitAmount(0.4f);
        topPanes.add(leftSplitPane).left().fill().expand();
        topPanes.add(rightSplitPane).right().fill().expand();


        SplitPane downSplitPane = new SplitPane(topPanes, tableBottom, true, skin.get("default-vertical", SplitPane.SplitPaneStyle.class)) {
            @Override
            public Actor hit(float x, float y, boolean touchable) {
                // Disable hit on empty area
                if(y > getHeight() * (1 - getSplit()) + getStyle().handle.getMinHeight() &&
                        x > leftSplitPane.getWidth() * leftSplitPane.getSplit() + leftSplitPane.getStyle().handle.getMinWidth() &&
                        x - leftSplitPane.getWidth() < rightSplitPane.getWidth() * rightSplitPane.getSplit() - rightSplitPane.getStyle().handle.getMinWidth())
                    return null;
                return super.hit(x, y, touchable);
            }
        };
        downSplitPane.setSplitAmount(0.7f);
        add(downSplitPane).fill().expand();
    }

    private Table createMenuTable() {
        Table table = new Table();
        Table paneTable = new Table();
        ScrollPane scrollPane = new ScrollPane(paneTable, skin);
        table.add(scrollPane).fill().expand();
        paneTable.align(Align.top);

        TextButton addBallButton = new TextButton("Ball", skin);
        addBallButton.addListener(new ClickListener(0) {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                editorManager.add(Ball.class);
            }
        });
        paneTable.add(addBallButton);

        TextButton addCloudButton = new TextButton("Cloud", skin);
        addCloudButton.addListener(new ClickListener(0) {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                editorManager.add(Cloud.class);
            }
        });
        paneTable.add(addCloudButton);

        paneTable.row();

        TextButton addEmittorButton = new TextButton("Emitter", skin);
        addEmittorButton.addListener(new ClickListener(0) {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                editorManager.add(Emitter.class);
            }
        });
        paneTable.add(addEmittorButton);

        paneTable.row();

        TextButton addHammerButton = new TextButton("Hammer", skin);
        addHammerButton.addListener(new ClickListener(0) {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                editorManager.add(Hammer.class);
            }
        });
        paneTable.add(addHammerButton);

        TextButton addGroundButton = new TextButton("Ground", skin);
        addGroundButton.addListener(new ClickListener(0) {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                editorManager.addGround();
            }
        });
        paneTable.add(addGroundButton);

        paneTable.row();

        TextButton addHouseButton = new TextButton("House", skin);
        addHouseButton.addListener(new ClickListener(0) {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                editorManager.add(House.class);
            }
        });
        paneTable.add(addHouseButton);

        TextButton addTriggerButton = new TextButton("Trigger", skin);
        addTriggerButton.addListener(new ClickListener(0) {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                editorManager.add(Trigger.class);
            }
        });
        paneTable.add(addTriggerButton);

        paneTable.row();

        TextButton addZoneButton = new TextButton("Zone", skin);
        addZoneButton.addListener(new ClickListener(0) {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                editorManager.add(Zone.class);
            }
        });
        paneTable.add(addZoneButton);

        paneTable.row();

        TextButton debugButton = new TextButton("Debug", skin.get(TextButton.TextButtonStyle.class));
        debugButton.addListener(new ClickListener(0) {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                stage.setDebug(!stage.isDebug());
            }
        });
        paneTable.add(debugButton);

        TextButton physicsButton = new TextButton("Physics", skin.get(TextButton.TextButtonStyle.class));
        physicsButton.addListener(new ClickListener(0) {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                stage.togglePhysics();
            }
        });
        paneTable.add(physicsButton);

        paneTable.row();

        final TextField filenameTextField = new TextField("temp.lua", skin);
        paneTable.add(filenameTextField).colspan(2);

        paneTable.row();

        TextButton saveButton = new TextButton("Save", skin.get(TextButton.TextButtonStyle.class));
        saveButton.addListener(new ClickListener(0) {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                editorManager.save(filenameTextField.getText());
            }
        });
        paneTable.add(saveButton);

        TextButton loadButton = new TextButton("Load", skin.get(TextButton.TextButtonStyle.class));
        loadButton.addListener(new ClickListener(0) {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                editorManager.load(filenameTextField.getText());
            }
        });
        paneTable.add(loadButton);

        paneTable.row();

        TextButton clearButton = new TextButton("Clear", skin.get(TextButton.TextButtonStyle.class));
        clearButton.addListener(new ClickListener(0) {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                stage.reset();
            }
        });
        paneTable.add(clearButton);

        return table;
    }

    private Table createPropertiesTable() {
        Table table = new Table();
        Table paneTable = new Table();
        ScrollPane scrollPane = new ScrollPane(paneTable, skin.get(ScrollPane.ScrollPaneStyle.class));
        table.add(scrollPane).fill().expand();
        paneTable.align(Align.top);

        Label nameLabel = new Label("Name:", skin.get(Label.LabelStyle.class));
        paneTable.add(nameLabel);

        nameTextField = new TextField("", skin.get(TextField.TextFieldStyle.class));
        paneTable.add(nameTextField);

        paneTable.row();

        Label posLabel = new Label("Pos:", skin.get(Label.LabelStyle.class));
        paneTable.add(posLabel);

        posTextField = new TextField("-,-", skin.get(TextField.TextFieldStyle.class));
        paneTable.add(posTextField);

        paneTable.row();

        Label sizeLabel = new Label("Size:", skin.get(Label.LabelStyle.class));
        paneTable.add(sizeLabel);

        sizeTextField = new TextField("-,-", skin.get(TextField.TextFieldStyle.class));
        paneTable.add(sizeTextField);

        paneTable.row();

        Label abgleLabel = new Label("Angle:", skin.get(Label.LabelStyle.class));
        paneTable.add(abgleLabel);

        angleTextField = new TextField("-", skin.get(TextField.TextFieldStyle.class));
        paneTable.add(angleTextField);

        paneTable.row();

        TextButton updatePropButton = new TextButton("Update", skin.get(TextButton.TextButtonStyle.class));
        updatePropButton.addListener(new ClickListener(0) {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                updateSelectedActor();
            }
        });
        paneTable.add(updatePropButton);

        TextButton resetPropButton = new TextButton("Reset", skin.get(TextButton.TextButtonStyle.class));
        resetPropButton.addListener(new ClickListener(0) {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                readActor(stage.getSelectedActor());
            }
        });
        paneTable.add(resetPropButton);

        paneTable.row();

        TextButton removeButton = new TextButton("Remove", skin.get(TextButton.TextButtonStyle.class));
        removeButton.addListener(new ClickListener(0) {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                editorManager.removeSelectedActor();
            }
        });
        paneTable.add(removeButton);

        paneTable.row();

        TextButton backButton = new TextButton("Back", skin.get(TextButton.TextButtonStyle.class));
        backButton.addListener(new ClickListener(0) {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                stage.getSelectedActor().toBack();
            }
        });
        paneTable.add(backButton);

        TextButton frontButton = new TextButton("Front", skin.get(TextButton.TextButtonStyle.class));
        frontButton.addListener(new ClickListener(0) {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                stage.getSelectedActor().toFront();
            }
        });
        paneTable.add(frontButton);

        paneTable.row();

        Label cursorPosLabel = new Label("Cursor:", skin.get(Label.LabelStyle.class));
        paneTable.add(cursorPosLabel);

        cursorPosValueLabel = new Label("-,-", skin.get(Label.LabelStyle.class));
        paneTable.add(cursorPosValueLabel);

        return table;
    }

    private Table createConsoleTable() {
        Table table = new Table();
        Table textEditorTable = new Table();
        ScrollPane scrollPane = new ScrollPane(textEditorTable, skin.get(ScrollPane.ScrollPaneStyle.class));
        table.add(scrollPane).fill().expand();

        Table buttonsTable = new Table();

        textEditorTable.add(buttonsTable).top();

        final Label label = new Label("", skin);
        textEditorTable.add(label).expand().top();

        TextButton onCheckButton = new TextButton("onCheck", skin.get(TextButton.TextButtonStyle.class));
        buttonsTable.add(onCheckButton);
        onCheckButton.addListener(new ClickListener(0) {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                label.setText(editorManager.onCheckStr);
            }
        });

        buttonsTable.row();

        TextButton onBeginContactButton = new TextButton("onBeginContact", skin.get(TextButton.TextButtonStyle.class));
        buttonsTable.add(onBeginContactButton);
        onBeginContactButton.addListener(new ClickListener(0) {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                label.setText(editorManager.onBeginContactStr);
            }
        });

        buttonsTable.row();

        TextButton onEndContactButton = new TextButton("onEndContact", skin.get(TextButton.TextButtonStyle.class));
        buttonsTable.add(onEndContactButton);
        onEndContactButton.addListener(new ClickListener(0) {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                label.setText(editorManager.onEndContactStr);
            }
        });

        return table;
    }

    public void readActor(SimpleActor actor) {
        String title;

        if(actor != null && actor.getName() != null)
            title = actor.getName();
        else if(actor != null && actor.getName() == null)
            title = "-unnamed-";
        else
            title = "-";

        nameTextField.setText(title);

        if(actor != null)
            title = floatFormat.format(actor.getX()) + "," + floatFormat.format(actor.getY());
        else
            title = "-,-";

        posTextField.setText(title);

        if(actor != null)
            title = floatFormat.format(actor.getWidth()) + "," + floatFormat.format(actor.getHeight());
        else
            title = "-,-";

        sizeTextField.setText(title);

        if(actor != null)
            title = String.valueOf(floatFormat.format(actor.getRotation()));
        else
            title = "-";

        angleTextField.setText(title);
    }

    private void updateSelectedActor() {
        SimpleActor selectedActor = stage.getSelectedActor();

        if(selectedActor == null)
            return;

        selectedActor.setName(nameTextField.getText());

        try {
            String text = sizeTextField.getText();
            Vector2 s = new Vector2(Float.valueOf(text.substring(0, text.indexOf(","))),
                    Float.valueOf(text.substring(text.indexOf(",") + 1)));
            selectedActor.setSize(s.x, s.y);

            text = posTextField.getText();
            Vector2 v = new Vector2(Float.valueOf(text.substring(0, text.indexOf(","))),
                    Float.valueOf(text.substring(text.indexOf(",") + 1)));
            selectedActor.setPosition(v.x, v.y);

            text = angleTextField.getText();
            selectedActor.setRotation(Float.valueOf(text));
        } catch (NumberFormatException e) {
            Logger.getGlobal().log(Level.WARNING, e.getMessage(), e);
        }
    }

    public void setCursorPositionValue(Vector2 v) {
        cursorPosValueLabel.setText(floatFormat.format(v.x) + "," + floatFormat.format(v.y));
    }
}
