package hibernate.v2.sunshine.api

import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject

abstract class BaseRepository {
    val disposables = CompositeDisposable()
    val sIsLoading = PublishSubject.create<Boolean>()
    val sError = PublishSubject.create<Throwable>()

    fun dispose() {
        disposables.dispose()
    }
}

interface RepoCallback<T> {
    fun onComplete(response: T)
    fun onError(error: Throwable)
}
