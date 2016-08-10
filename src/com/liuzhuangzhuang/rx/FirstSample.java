package com.liuzhuangzhuang.rx;

import rx.Observer;
import rx.Subscriber;
import rx.observables.SyncOnSubscribe;
import rx.schedulers.Schedulers;

public class FirstSample {

	public static void main(String[] args) {
		System.out.println("currentThread : " + Thread.currentThread());
		
		rx.Observer<String> observer = new rx.Observer<String>() {

			@Override
			public void onCompleted() {

			}

			@Override
			public void onError(Throwable arg0) {

			}

			@Override
			public void onNext(String arg0) {

			}
		};

		// ��observer���ḻ
		Subscriber<Integer> subscriber = new Subscriber<Integer>() {

			@Override
			public void onStart() {
				super.onStart();
			}
			
			@Override
			public void onCompleted() {
				System.out.println("subcriber onCompleted");
			}

			@Override
			public void onError(Throwable throwable) {
				System.out.println("subcriber onError : " + throwable.getMessage());
			}

			@Override
			public void onNext(Integer integer) {
				System.out.println("subsciber onNext : " + integer);
				System.out.println("currentThread : " + Thread.currentThread());
			}
		};
		
		rx.Observable<Integer> observable = rx.Observable.create(new SyncOnSubscribe<String, Integer>() {

			@Override
			protected String generateState() {
				return "joke";
			}

			@Override
			protected String next(String state, Observer<? super Integer> observer) {
				System.out.println(state);
				observer.onNext(1);
				observer.onCompleted();
				System.out.println("currentThread : " + Thread.currentThread());
				return null;
			}
		});
		
		observable.subscribeOn(Schedulers.computation());
		observable.observeOn(Schedulers.newThread());

		observable.subscribe(subscriber);
	}
}
