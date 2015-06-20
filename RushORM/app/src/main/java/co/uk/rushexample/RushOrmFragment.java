package co.uk.rushexample;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import co.uk.rushexample.demo.Car;
import co.uk.rushexample.demo.Engine;
import co.uk.rushexample.demo.Wheel;
import co.uk.rushorm.core.RushCallback;
import co.uk.rushorm.core.RushCore;
import co.uk.rushorm.core.RushPageList;
import co.uk.rushorm.core.RushSearch;

/**
 * Created by Stuart on 31/01/15.
 */
public class RushOrmFragment extends Fragment {

    public RushOrmFragment() {
        // Required empty public constructor
    }

    public String getName() {
        return "RushOrm";
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_base, container, false);
        ((TextView) view.findViewById(R.id.base_fragment_title)).setText(getName());
        view.findViewById(R.id.button).setOnClickListener(saveListener);
        view.findViewById(R.id.button_load).setOnClickListener(loadListener);
        view.findViewById(R.id.button_delete).setOnClickListener(deleteListener);
        return view;
    }

    private View.OnClickListener saveListener = new View.OnClickListener() {
        @Override
        public void onClick(final View view) {

            getView().findViewById(R.id.loaded_textView).setVisibility(View.GONE);

            EditText editText = (EditText) getView().findViewById(R.id.editText);
            String carsRaw = editText.getText().toString();
            if (!carsRaw.isEmpty()) {

                setLoading(true);
                ((TextView) getView().findViewById(R.id.result_textView)).setText("Saving..");
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);

                final long startTime = System.currentTimeMillis();
                final int numberToSave = Integer.parseInt(carsRaw);

                List<Car> cars = new ArrayList<>(numberToSave);
                for(int i = 0; i < numberToSave; i ++) {
                    Car car = new Car("Red", new Engine());
                    car.wheels = new ArrayList<>();
                    for (int j = 0; j < 4; j ++) {
                        car.wheels.add(new Wheel("Michelin"));
                    }
                    cars.add(car);
                }

                RushCore.getInstance().save(cars, new RushCallback() {
                    @Override
                    public void complete() {
                        long endTime = System.currentTimeMillis();
                        final double saveTime = (endTime - startTime) / 1000.0;
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ((TextView) getView().findViewById(R.id.result_textView)).setText(String.format("%1$,.2f s", saveTime));
                                setLoading(false);
                            }
                        });
                    }
                });
            } else {
                editText.setError("Can not be empty");
            }
        }
    };

    private View.OnClickListener loadListener = new View.OnClickListener() {
        @Override
        public void onClick(final View view) {

            setLoading(true);
            ((TextView) getView().findViewById(R.id.result_textView)).setText("Loading..");
            final long startTime = System.currentTimeMillis();
            new Thread() {
                @Override
                public void run() {

                    // Loads all full objects
                    List<Car> cars = new RushSearch().find(Car.class);

                    // Lazy loads items as you go through the list iteal for large data sets
                    //RushPageList<Car> cars = new RushPageList<>(Car.class);

                    int count = cars.size();

                    final int loading = count;
                    getActivity().runOnUiThread(new Runnable() {
                        public void run() {
                            long endTime = System.currentTimeMillis();
                            ((TextView) getView().findViewById(R.id.loaded_textView)).setText("Loaded - " + Integer.toString(loading));
                            double saveTime = (endTime - startTime) / 1000.0;
                            ((TextView) getView().findViewById(R.id.result_textView)).setText(String.format("%1$,.2f s", saveTime));
                            setLoading(false);
                            getView().findViewById(R.id.loaded_textView).setVisibility(View.VISIBLE);
                        }
                    });
                }
            }.start();
        }
    };

    private View.OnClickListener deleteListener = new View.OnClickListener() {
        @Override
        public void onClick(final View view) {

            setLoading(true);
            ((TextView) getView().findViewById(R.id.result_textView)).setText("Deleting..");
            final long startTime = System.currentTimeMillis();

            new Thread() {
                @Override
                public void run() {
                    RushCore.getInstance().deleteAll(Car.class);
                    RushCore.getInstance().deleteAll(Engine.class);
                    RushCore.getInstance().deleteAll(Wheel.class);
                    getActivity().runOnUiThread(new Runnable() {
                        public void run() {
                            long endTime = System.currentTimeMillis();
                            double saveTime = (endTime - startTime) / 1000.0;
                            ((TextView) getView().findViewById(R.id.result_textView)).setText(String.format("%1$,.2f s", saveTime));
                            setLoading(false);
                        }
                    });
                }
            }.start();
        }
    };

    private void setLoading(boolean loading) {
        getView().findViewById(R.id.button).setEnabled(!loading);
        getView().findViewById(R.id.button_load).setEnabled(!loading);
        getView().findViewById(R.id.button_delete).setEnabled(!loading);

        if (loading) {
            getView().findViewById(R.id.loaded_textView).setVisibility(View.GONE);
        }
    }
}
