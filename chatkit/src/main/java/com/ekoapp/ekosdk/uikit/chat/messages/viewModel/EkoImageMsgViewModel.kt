package com.ekoapp.ekosdk.uikit.chat.messages.viewModel

import android.util.Log
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import com.ekoapp.ekosdk.EkoClient
import com.ekoapp.ekosdk.EkoFileRepository
import com.ekoapp.ekosdk.file.EkoImage
import com.ekoapp.ekosdk.message.EkoMessage
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.io.File

class EkoImageMsgViewModel : EkoSelectableMessageViewModel() {

    val imageUrl = ObservableField<String>("")
    val uploading = ObservableBoolean(false)
    val uploadProgress = ObservableField(0)

    init {
        uploadProgress.addOnPropertyChanged {
            if (uploadProgress.get() == 100) {
                uploading.set(false)
            } else {
                uploading.set(true)
            }
        }
    }

    fun getImageUploadProgress(ekoMessage: EkoMessage) {
        val imageData = ekoMessage.getData() as EkoMessage.Data.IMAGE
        val localPath = imageData.getImage()?.getFilePath()
        if (localPath != null) {
            val file = File(localPath)
            if (file.exists() && imageUrl.get() != localPath) {
                imageUrl.set(localPath)
            }
        } else {
            if (ekoMessage.getState() == EkoMessage.State.SYNCED) {
                if (imageUrl.get() != imageData.getImage()?.getUrl(EkoImage.Size.MEDIUM)) {
                    imageUrl.set(imageData.getImage()?.getUrl(EkoImage.Size.MEDIUM))
                }
            } else {
                if (ekoMessage.getState() == EkoMessage.State.UPLOADING) {
                    val fileRepository: EkoFileRepository = EkoClient.newFileRepository()
                    addDisposable(fileRepository.getUploadInfo(ekoMessage.getMessageId())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnNext { uploadInfo ->
                            uploadProgress.set(uploadInfo.getProgressPercentage())
                        }.doOnError {
                            Log.e("EkoImageMsgViewModel", "Error ${it.localizedMessage}")
                        }.subscribe()
                    )
                }
            }
        }
    }

}