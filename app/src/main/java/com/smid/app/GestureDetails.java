package com.smid.app;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.smid.app.smid.R;
import com.smid.app.model.businessLogic.Action;
import com.smid.app.model.businessLogic.Gesture;
import com.smid.app.modelManagers.ActionModelManager;
import com.smid.app.modelManagers.ExternalServiceWithSameComponentAlreadyExists;
import com.smid.app.modelManagers.GestureModelManager;
import com.smid.app.modelManagers.GestureNameAlreadyExistsException;
import com.smid.app.modelManagers.NeedsPaymentException;
import com.smid.app.modelManagers.NullGestureNameException;
import com.smid.app.payments.PaymentHelper;

import java.util.List;

public class GestureDetails extends BaseActivity {
    Gesture gesture;

    GestureModelManager gestureModelManager;
    ActionModelManager actionModelManager;

    TextView etGestureName;
    TextView txActionAssociation;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            setContentView(R.layout.gesture_details);

            // create model

            gestureModelManager = new GestureModelManager(getBaseContext());
            actionModelManager = new ActionModelManager(getBaseContext());

            getModelAndPresentIt();
        } catch (Exception ex) {
            Helper.logException(this, ex);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        getModelAndPresentIt();
    }

    void getModelAndPresentIt(){
        Intent i = getIntent();

        Bundle b = i.getExtras();

        if(b!= null) {
            Object gesture_id_obj = b.get(GD_CONSTS.GestureID);

            if(gesture_id_obj != null) {
                Long gesture_id = (Long) gesture_id_obj;

                gesture = gestureModelManager.GetItemById(gesture_id);
            }
        }

        // Set layout
        prepareView();
    }

    public void onClick(View v) {
        if (v.getId() == R.id.GestureDetails_BtnChangeAction) {
            try {
                Intent intent = new Intent(this, ActionListToSelect.class);

                intent.putExtra(GD_CONSTS.WhereUserIsText, getString(R.string.GestureDetails_Flow_SelectActionAssociated) + " " + gesture.getName() + " "
                        + getString(R.string.GestureDetails_Flow_SelectActionAssociated_part2));

                startActivityForResult(intent, GD_CONSTS.SelectActivityRequestCode);
            } catch (Exception e) {
                Helper.logException(this, e);
            }
        } else if (v.getId() == R.id.GestureDetails_BtnChangeName) {
            EditText et = getGestureNameET();

            et.setText(gesture.getName());
            et.setFocusable(true);

            et.setVisibility(View.VISIBLE);
            getBtnConfirmChangeName().setVisibility(View.VISIBLE);
            getBtnCancelChangeName().setVisibility(View.VISIBLE);

            getGestureNameTV().setVisibility(View.GONE);
            getBtnChangeName().setVisibility(View.GONE);

        } else if (v.getId() == R.id.GestureDetails_BtnCancelChangeName) {
            cancelStopEditingText();

        } else if (v.getId() == R.id.GestureDetails_BtnConfirmChangeName) {
            String originalName = gesture.getName();

            gesture.setName(getGestureNameET().getText().toString());

            try {
                gestureModelManager.Persist(gesture);
            } catch (NeedsPaymentException e) {
                PaymentHelper.showAlertNeedsBuying(this);
            } catch (GestureNameAlreadyExistsException ex) {
                String alreadyExistingName = gesture.getName();

                gesture.setName(originalName);

                AlertDialog.Builder builder = new AlertDialog.Builder(GestureDetails.this);

                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                };

                builder.setMessage(Helper.getStringForAlreadyExistingGestureName(this, alreadyExistingName))
                        .setPositiveButton(R.string.ok, dialogClickListener)
                        .create().show();

                return;
            } catch (NullGestureNameException e) {
                AlertDialog.Builder builder = new AlertDialog.Builder(GestureDetails.this);

                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                };

                builder.setMessage(getString(R.string.GestureNameMustNotBeEmpty))
                        .setPositiveButton(R.string.ok, dialogClickListener)
                        .create().show();

                return;
            } catch (ExternalServiceWithSameComponentAlreadyExists externalServiceWithSameComponentAlreadyExists) {
                Helper.logException(this, externalServiceWithSameComponentAlreadyExists);
            }

            cancelStopEditingText();
        } else if (v.getId() == R.id.GestureDelete) {
            AlertDialog.Builder builder = new AlertDialog.Builder(GestureDetails.this);

            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                        case DialogInterface.BUTTON_POSITIVE:
                            gestureModelManager.Delete(gesture);

                            Helper.startMainMenu(GestureDetails.this);

                            break;
                        case DialogInterface.BUTTON_NEGATIVE:
                            break;
                    }
                }
            };

            builder.setMessage(getStringTryingToDeleteGestureNamed(gesture.getName()))
                    .setPositiveButton("Yes", dialogClickListener)
                    .setNegativeButton("No", dialogClickListener)
                    .create().show();
        } else if (v.getId() == R.id.GestureBackToMainMenu) {
            Helper.startMainMenu(this);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GD_CONSTS.SelectActivityRequestCode) {
            if (resultCode == RESULT_OK) {
                Bundle b = data.getExtras();

                Long actionId = (Long) b.get(GD_CONSTS.SelectedActionID);

                gesture.setActionID(actionId);

                try {
                    gestureModelManager.Persist(gesture);
                } catch(NeedsPaymentException ex) {
                    PaymentHelper.showAlertNeedsBuying(this);

                    return;
                } catch (GestureNameAlreadyExistsException e) {
                    Helper.logException(this, e);
                } catch (NullGestureNameException e) {
                    Helper.logException(this, e);
                } catch (ExternalServiceWithSameComponentAlreadyExists externalServiceWithSameComponentAlreadyExists) {
                    Helper.logException(this, externalServiceWithSameComponentAlreadyExists);
                }
            }
        }
    }

    public void prepareView() {
        ViewGroup gridLayout = getGestureDetailsLayout();

        txActionAssociation = (TextView) gridLayout.findViewById(R.id.GestureDetails_ActionNameLayout).findViewById(R.id.GestureActionAssociationValue);

        getGestureNameTV().setText(gesture.getName());

        LinearLayout gestureMoveLayout = (LinearLayout) getGestureDetailsLayout().findViewById(R.id.GestureDetails_GestureMoveLayout);
        gestureMoveLayout.removeAllViews();

        MotionImagePresenter motionImagePresenter = new MotionImagePresenter();
        List<ImageView> images = motionImagePresenter.transformMoveToImages(gesture.getMove(), this);
        for(int i=0;i<images.size();i++){
            gestureMoveLayout.addView(images.get(i));
        }

        if(gesture.getActionID() >= 0) {
            Action action = actionModelManager.GetItemById(gesture.getActionID());
            getTxActionAssociation().setText(action.GetTextRepresentation(this));
        } else {
            getTxActionAssociation().setText("No action is associated with gesture");
        }

        Helper.setTopText(this);
    }
    protected TextView getTxActionAssociation() {
        return txActionAssociation;
    }

    ViewGroup getGestureDetailsLayout (){
        return (ViewGroup) findViewById(R.id.GestureName_MainLayout).findViewById(R.id.GestureDetailsView);
    }

    Button getBtnCancelChangeName () {
        return (Button) getGestureDetailsLayout()
                .findViewById(R.id.GestureDetails_GestureNameButtons)
                .findViewById(R.id.GestureDetails_NameLayout)
                .findViewById(R.id.GestureDetails_BtnCancelChangeName);
    }

    Button getBtnConfirmChangeName () {
        return (Button) getGestureDetailsLayout()
                .findViewById(R.id.GestureDetails_GestureNameButtons)
                .findViewById(R.id.GestureDetails_NameLayout)
                .findViewById(R.id.GestureDetails_BtnConfirmChangeName);
    }

    Button getBtnChangeName () {
        return (Button) getGestureDetailsLayout()
                .findViewById(R.id.GestureDetails_GestureNameButtons)
                .findViewById(R.id.GestureDetails_NameLayout)
                .findViewById(R.id.GestureDetails_BtnChangeName);
    }

    TextView getGestureNameTV () {
        return ((TextView) gestureNameLayout().findViewById(R.id.GestureNameValue));
    }

    ViewGroup gestureNameLayout() {
        return (ViewGroup) getGestureDetailsLayout().findViewById(R.id.GestureDetails_GestureNameLayout);
    }

    EditText getGestureNameET () {
        return ((EditText) getGestureDetailsLayout().findViewById(R.id.GestureDetails_GestureNameLayout)
                .findViewById(R.id.GestureNameValue_EditText));
    }

    TextView getGestureMoveTV() {
        return null;//((TextView) getGestureDetailsLayout().findViewById(R.id.Gesture_Move_Value));
    }

    void cancelStopEditingText () {
        getGestureNameTV().setText(gesture.getName());
        getGestureNameTV().setVisibility(View.VISIBLE);
        getBtnChangeName().setVisibility(View.VISIBLE);

        getGestureNameET().setVisibility(View.GONE);
        getBtnCancelChangeName().setVisibility(View.GONE);
        getBtnConfirmChangeName().setVisibility(View.GONE);
    }



    public String getStringTryingToDeleteGestureNamed(String gestureName) {
        StringBuilder sb =new StringBuilder();

        sb.append(getString(R.string.trying_to_delete_gesture_part_1));
        sb.append(" ");
        sb.append(gestureName);
        sb.append(" ");
        sb.append(getString(R.string.trying_to_delete_gesture_part_2));

        return sb.toString();
    }
}
