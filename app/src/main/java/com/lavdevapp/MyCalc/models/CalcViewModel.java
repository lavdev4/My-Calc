package com.lavdevapp.MyCalc.models;

import android.app.Application;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.InstanceCreator;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializer;
import com.lavdevapp.MyCalc.R;

import java.lang.reflect.MalformedParameterizedTypeException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class CalcViewModel extends AndroidViewModel {
    private LiveData<String> screenText;
    private LiveData<String> positionScreenText;
    private InputController inputController;
    private final SharedPreferences sharedPreferences;
    private boolean saveRequired = false;

    private final String SHARED_PREFS_NAME = "AppPreferences";
    private final String SHARED_PREFS_INPUT_CONTROLLER_KEY = "input_controller";

    public CalcViewModel(Application context) {
        super(context);
        sharedPreferences = context
                .getSharedPreferences(SHARED_PREFS_NAME, AppCompatActivity.MODE_PRIVATE);
        if (sharedPreferences.contains(SHARED_PREFS_INPUT_CONTROLLER_KEY)) {
            loadData();
        } else {
            loadDefault();
        }
    }

    public LiveData<String> getScreenText() {
        return screenText;
    }

    public LiveData<String> getPositionScreenText() {
        return positionScreenText;
    }

    public void onButtonPressed(int buttonId) {
        if (buttonId == R.id.oneButton) {
            saveRequired = inputController.init("1");
        } else if (buttonId == R.id.twoButton) {
            saveRequired = inputController.init("2");
        } else if (buttonId == R.id.threeButton) {
            saveRequired = inputController.init("3");
        } else if (buttonId == R.id.fourButton) {
            saveRequired = inputController.init("4");
        } else if (buttonId == R.id.fiveButton) {
            saveRequired = inputController.init("5");
        } else if (buttonId == R.id.sixButton) {
            saveRequired = inputController.init("6");
        } else if (buttonId == R.id.sevenButton) {
            saveRequired = inputController.init("7");
        } else if (buttonId == R.id.eightButton) {
            saveRequired = inputController.init("8");
        } else if (buttonId == R.id.nineButton) {
            saveRequired = inputController.init("9");
        } else if (buttonId == R.id.zeroButton) {
            saveRequired = inputController.init("0");
        } else if (buttonId == R.id.plusButton) {
            saveRequired = inputController.init("+");
        } else if (buttonId == R.id.minusButton) {
            saveRequired = inputController.init("-");
        } else if (buttonId == R.id.multiplyButton) {
            saveRequired = inputController.init("*");
        } else if (buttonId == R.id.divideButton) {
            saveRequired = inputController.init("/");
        } else if (buttonId == R.id.leftBraceButton) {
            saveRequired = inputController.init("(");
        } else if (buttonId == R.id.rightBraceButton) {
            saveRequired = inputController.init(")");
        } else if (buttonId == R.id.removeButton) {
            saveRequired = inputController.remove();
        } else if (buttonId == R.id.commaButton) {
            saveRequired = inputController.init(".");
        } else if (buttonId == R.id.equalsButton) {
            saveRequired = inputController.init("=");
        } else if (buttonId == R.id.clearButton) {
            inputController.clearAllCalculations();
            saveRequired = true;
        }
    }

    public void saveData() {
        if (saveRequired) {
            Gson gson = new GsonBuilder()
                    .registerTypeHierarchyAdapter(LiveData.class, createLiveDataSerializer())
                    .create();
            String serializedInputController = gson.toJson(inputController);
            Log.d("Json: ", serializedInputController);
            sharedPreferences.edit()
                    .putString(SHARED_PREFS_INPUT_CONTROLLER_KEY, serializedInputController)
                    .apply();
            saveRequired = false;
        }
    }

    private void loadData() {
        String serializedInputController = sharedPreferences.getString(SHARED_PREFS_INPUT_CONTROLLER_KEY, null);
        if (serializedInputController != null) {
            Gson gson = new GsonBuilder()
                    .registerTypeHierarchyAdapter(LiveData.class, createLiveDataDeserializer())
                    .registerTypeAdapter(CalculatorRPN.class, createCalculatorRpnInstanceCreator())
                    .create();
            inputController = gson.fromJson(serializedInputController, InputController.class);
            Log.d("Json: ", inputController.toString());
            screenText = inputController.getCalculationsHistory();
            positionScreenText = inputController.getCurrentPosition();
        } else {
            loadDefault();
        }
    }

    private void loadDefault() {
        inputController = new InputController();
        screenText = inputController.getCalculationsHistory();
        positionScreenText = inputController.getCurrentPosition();
    }

    private JsonSerializer<LiveData<String>> createLiveDataSerializer() {
        return (src, typeOfSrc, context) -> new JsonPrimitive(src.getValue());
    }

    private JsonDeserializer<LiveData<String>> createLiveDataDeserializer() {
        return (json, typeOfT, context) -> {
            try {
                Type[] typeParameters = ((ParameterizedType) typeOfT).getActualTypeArguments();
                if (typeParameters.length != 1) {
                    throw new JsonParseException(
                            "Wrong number of generic type parameters. Must be single type - String."
                    );
                }
                Type genericClass = typeParameters[0];
                if (genericClass != String.class) {
                    throw new JsonParseException(
                            "Incompatible type of generic class: " + genericClass + ". " + "Required class: String."
                    );
                } else {
                    return new MutableLiveData<>(json.getAsJsonPrimitive().getAsString());
                }
            } catch (ClassCastException nonGenericClassException) {
                throw new JsonParseException("Deserializable class is not a generic class.");
            } catch (TypeNotPresentException | MalformedParameterizedTypeException parametrizedTypeException) {
                throw new JsonParseException("Generic class malformed or unknown.");
            }
        };
    }

    private InstanceCreator<CalculatorRPN> createCalculatorRpnInstanceCreator() {
        return type -> {
            if (type != CalculatorRPN.class) {
                throw new JsonParseException(
                        "Instance creator is used on a wrong class - " +
                        type +
                        ". " +
                        "Expected: CalculatorRPN.class"
                );
            } else {
                return new CalculatorRPN();
            }
        };
    }
}
