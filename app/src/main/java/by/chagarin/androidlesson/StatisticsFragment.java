package by.chagarin.androidlesson;

import android.app.Fragment;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import by.chagarin.androidlesson.views.PieChartView;

@EFragment(R.layout.fragment_statistics)
public class StatisticsFragment extends Fragment {
    private float[] dataPoints = {400, 50, 70, 90, 100};

    @ViewById(R.id.pie_diagramm)
    PieChartView pieDiagramm;

    @AfterViews
    void ready() {
        pieDiagramm.setDataPoints(dataPoints);
    }
}
