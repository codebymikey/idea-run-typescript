package io.plugin.tsnode.lib

//import com.intellij.javascript.nodejs.util.NodePackageField
import com.intellij.execution.configuration.EnvironmentVariablesTextFieldWithBrowseButton
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.TextFieldWithBrowseButton
import com.intellij.openapi.util.text.StringUtil
import com.intellij.ui.RawCommandLineEditor
import com.intellij.ui.TextAccessor
import com.intellij.util.ui.FormBuilder
import com.intellij.util.ui.SwingHelper
import javax.swing.JComponent

class TsForm
{
	open class TsFormBuilder : com.intellij.util.ui.FormBuilder()
	{
		fun append(field: Field<*>) = field.appendTo(this) as TsFormBuilder

		fun append(field: JComponent, label: String) = addLabeledComponent(label, field) as TsFormBuilder

		fun append(field: Field<*>, label: String) = this.addLabeledComponent(label, field.field as JComponent) as TsFormBuilder

		override fun addLabeledComponent(label: String, field: JComponent) = super.addLabeledComponent(label, field) as TsFormBuilder

		fun addLabeledComponent(label: String, field: Field<*>) = this.addLabeledComponent(label, field.field as JComponent)

		fun addLabeledComponent(field: Field<*>) = this.addLabeledComponent(field.label, field.field as JComponent)

		override fun setAlignLabelOnRight(alignLabelOnRight: Boolean) = super.setAlignLabelOnRight(alignLabelOnRight) as TsFormBuilder
	}

	interface Field<T>
	{
		val field: T
		val label: String

		fun appendTo(form: FormBuilder) = form.addLabeledComponent(label, field as JComponent)
		fun <F : TsFormBuilder> appendTo(form: F) = form.addLabeledComponent(label, field as JComponent) as F
	}

	open class TextField<Comp : TextAccessor>(override val field: Comp, override val label: String) : Field<Comp>
	{
		var text
			get() = this.field.text
			set(value)
			{
				this.field.text = value
			}
	}

	open class RawCommandLineEditorField<Comp : RawCommandLineEditor>(override val field: Comp, override val label: String) : TextField<Comp>(field, label)
	{
		init
		{
			if (StringUtil.isEmptyOrSpaces(field.dialogCaption))
			{
				field.dialogCaption = Util.stripTitle(label)
			}
		}

		var dialogCaption
			get() = this.field.dialogCaption
			set(value)
			{
				this.field.dialogCaption = value
			}
	}

	open class NodePackageField<Comp : com.intellij.javascript.nodejs.util.NodePackageField>(override val field: Comp, override val label: String) : Field<Comp>
	{
		var selected
			get() = this.field.selected
			set(value)
			{
				this.field.selected = value
			}

		var selectedRef
			get() = this.field.selectedRef
			set(value)
			{
				this.field.selectedRef = value
			}
	}

	open class NodeJsInterpreterField<Comp : com.intellij.javascript.nodejs.interpreter.NodeJsInterpreterField>(override val field: Comp, override val label: String) : Field<Comp>
	{
		var interpreter
			get() = this.field.interpreter
			set(value)
			{
				this.field.interpreter = value
			}

		var interpreterRef
			get() = this.field.interpreterRef
			set(value)
			{
				this.field.interpreterRef = value
			}
	}

	open class EnvironmentVariablesTextFieldWithBrowseButtonField<Comp : com.intellij.execution.configuration.EnvironmentVariablesTextFieldWithBrowseButton>(override val field: Comp, override val label: String) : Field<Comp>
	{
		//fun isPassParentEnvs() = field.isPassParentEnvs

		val isPassParentEnvs
		get() = field.isPassParentEnvs

		var data
			get() = this.field.data
			set(value)
			{
				this.field.data = value
			}

		var envs
			get() = this.field.envs
			set(value)
			{
				this.field.envs = value
			}
	}

	companion object
	{

		fun LazyNodeJsInterpreterField(label: String, project: Project, withRemote: Boolean = false, fieldFactory: com.intellij.javascript.nodejs.interpreter.NodeJsInterpreterField = com.intellij.javascript.nodejs.interpreter.NodeJsInterpreterField(project, withRemote)) = NodeJsInterpreterField(fieldFactory, label)

		fun LazyTextFieldWithBrowseButton(label: String, fieldFactory: TextFieldWithBrowseButton = TextFieldWithBrowseButton()) = TextField(fieldFactory, label)

		fun LazyTextFieldWithBrowseSingleFolderButton(label: String, fieldFactory: TextFieldWithBrowseButton) = TextField(fieldFactory, label)

		fun LazyTextFieldWithBrowseSingleFolderButton(label: String, project: Project, browseDialogTitle: String, fieldFactory: TextFieldWithBrowseButton = Util.createWorkingDirectoryField(project, browseDialogTitle)) = TextField(fieldFactory, label)

		fun LazyTextFieldWithBrowseSingleFolderButton(label: String, project: Project, fieldFactory: TextFieldWithBrowseButton = Util.createWorkingDirectoryField(project, label)) = TextField(fieldFactory, label)

		fun LazyRawCommandLineEditor(label: String, fieldFactory: RawCommandLineEditor = com.intellij.ui.RawCommandLineEditor()) = RawCommandLineEditorField(fieldFactory, label)

		fun LazyNodePackageField(label: String, interpreterField: com.intellij.javascript.nodejs.interpreter.NodeJsInterpreterField, packageName: String, fieldFactory: com.intellij.javascript.nodejs.util.NodePackageField = com.intellij.javascript.nodejs.util.NodePackageField(interpreterField, packageName)) = NodePackageField(fieldFactory, label)

		fun LazyNodePackageField(
			label: String
			, interpreterField: NodeJsInterpreterField<com.intellij.javascript.nodejs.interpreter.NodeJsInterpreterField>
			, packageName: String, fieldFactory: com.intellij.javascript.nodejs.util.NodePackageField
			= com.intellij.javascript.nodejs.util.NodePackageField(
					interpreterField.field
					, packageName
				)
		) = NodePackageField(fieldFactory, label)

		fun LazyNodePackageField(label: String, fieldFactory: com.intellij.javascript.nodejs.util.NodePackageField) = NodePackageField(fieldFactory, label)

		fun LazyEnvironmentVariablesTextFieldWithBrowseButton(label: String, fieldFactory: EnvironmentVariablesTextFieldWithBrowseButton = EnvironmentVariablesTextFieldWithBrowseButton()) = EnvironmentVariablesTextFieldWithBrowseButtonField(fieldFactory, label)

	}

	object Util
	{
		fun createWorkingDirectoryField(project: Project, browseDialogTitle: String = "Select Path"): TextFieldWithBrowseButton
		{
			val field = TextFieldWithBrowseButton()

			SwingHelper
				.installFileCompletionAndBrowseDialog(project, field, stripTitle(browseDialogTitle),
					FileChooserDescriptorFactory.createSingleFolderDescriptor())

			return field
		}

		fun stripTitle(title: String): String
		{
			return title
				.replace("&(\\w)".toRegex(), "$1")
				.replace(":\\s*$".toRegex(), "")
		}
	}
}
