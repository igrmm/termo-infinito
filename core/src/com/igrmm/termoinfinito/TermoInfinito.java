package com.igrmm.termoinfinito;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class TermoInfinito extends ApplicationAdapter {
	public static final String[] KEYS = {"Q", "W", "E", "R", "T", "Y", "U", "I", "O", "P", "A", "S", "D", "F", "G", "H",
			"J", "K", "L", "<=", "Z", "X", "C", "V", "B", "N", "M", "ENTER"};
	private Stage stage;

	@Override
	public void create() {
		stage = new Stage(new ScreenViewport());
		Skin skin = new Skin(Gdx.files.internal("ui/clean-crispy-ui.json"));
		Table root = new Table();
		root.setFillParent(true);
		stage.addActor(root);
		Gdx.input.setInputProcessor(stage);

		//title
		Table titleTable = new Table();
		Label titleLabel = new Label("Termo Inifinito", skin);
		titleTable.add(titleLabel);
		root.add(titleTable).row();

		//words
		Table wordsTable = new Table();
		TextButton textButton = new TextButton(" ", skin, "default");
		TextButton textButton2 = new TextButton(" ", skin, "default");
		wordsTable.add(textButton2);
		wordsTable.add(textButton);
		root.add(wordsTable);
		root.add(new Actor()).expandY().row();

		//keyboard
		Table keyboardTable = new Table();
		Table firstRow = new Table();
		Table secondRow = new Table();
		Table thirdRow = new Table();
		float emptyCellWidth = 0.05f * Gdx.graphics.getWidth();

		for (int key = 0; key < KEYS.length; key++) {
			final TextButton keyButton = new TextButton(KEYS[key], skin);
			keyButton.addListener(new ClickListener() {
				@Override
				public void clicked(InputEvent event, float x, float y) {
					System.out.println(keyButton.getLabel().getText());
				}
			});

			//to P
			if (key <= 9) {
				firstRow.add(keyButton);
				if (key == 9) {
					firstRow.add(new Actor()).width(emptyCellWidth);
					keyboardTable.add(firstRow).row();
				}
			}

			//to <=
			if (key > 9 && key <= 19) {
				if (key == 10) {
					Actor emptyCell = new Actor();
					emptyCell.setWidth(emptyCellWidth);
					secondRow.add(emptyCell);
				}
				secondRow.add(keyButton);
				if (key == 18) {
					Actor emptyCell = new Actor();
					emptyCell.setWidth(emptyCellWidth);
					secondRow.add(emptyCell);
				}
				if (key == 19)
					keyboardTable.add(secondRow).row();
			}

			//to enter
			if (key > 19) {
				if (key == 20) {
					Actor emptyCell = new Actor();
					emptyCell.setWidth(emptyCellWidth);
					thirdRow.add(emptyCell);
				}
				if (key == 27)
					thirdRow.add(new Actor()).width(emptyCellWidth);
				thirdRow.add(keyButton);
				keyButton.right();
				if (key == 27) {
					thirdRow.add(new Actor()).width(emptyCellWidth);
					keyboardTable.add(thirdRow);
				}
			}
		}
		root.add(keyboardTable);
	}

	@Override
	public void render() {
		ScreenUtils.clear(191f, 191f, 191f, 1);
//		stage.setDebugAll(true);
		stage.act(Gdx.graphics.getDeltaTime());
		stage.draw();
	}

	@Override
	public void resize(int width, int height) {
		stage.getViewport().update(width, height, true);
	}

	@Override
	public void dispose() {
		stage.dispose();
	}
}
