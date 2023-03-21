package dk.itu.moapd.scootersharing.jonli

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.DialogFragment

class DeleteScooterFragment : DialogFragment() {

    private lateinit var listener: DeleteScooterListener

    interface DeleteScooterListener {
        fun onDialogPositiveClick(dialog: DialogFragment)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        super.onCreateDialog(savedInstanceState)
        val builder = AlertDialog.Builder(requireContext())
        arguments?.let {
            val scooterName = it.getString("arg")

            builder.setTitle("Delete scooter")
                .setMessage("Are you sure you want to delete $scooterName?")
                .setPositiveButton("Yes") { _, _ ->
                    listener.onDialogPositiveClick(this)
                }
                .setNegativeButton("No") { _, _ ->
                    dismiss()
                }
        }
        return builder.create()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            listener = context as DeleteScooterListener
        } catch (e: ClassCastException) {
            throw ClassCastException(
                (
                    context.toString() +
                        " must implement DeleteScooterListener"
                    ),
            )
        }
    }

    companion object {
        fun newInstance(arg: String): DeleteScooterFragment {
            val fragment = DeleteScooterFragment()
            val args = Bundle().apply {
                putString("arg", arg)
            }
            fragment.arguments = args
            return fragment
        }
        const val TAG = "DeleteScooterFragment"
    }
}
