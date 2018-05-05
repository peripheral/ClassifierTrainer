package com.app.tests;

import com.app.entities.Posture;
import com.app.graphics.body_model.BodyModelImpl;
import com.app.graphics.body_model.IBodyModel;

public class TestBodyModel {
	public static void main(String args[]){
		Posture p = new Posture();
		IBodyModel model = new BodyModelImpl();
		System.out.println("Angles:"+model.getA1Angle(p));
		System.out.println("Angles:"+model.getA2Angle(p));
		System.out.println("Angles:"+model.getA3Angle(p));
		System.out.println("Angles:"+model.getA4Angle(p));
		System.out.println("Angles:"+model.getA5Angle(p));
		System.out.println("Angles:"+model.getA6Angle(p));
		System.out.println("Angles:"+model.getA7Angle(p));
		System.out.println("Angles:"+model.getA8Angle(p));
		System.out.println("Angles:"+model.getA9Angle(p));
		System.out.println("Angles:"+model.getA10Angle(p));
		System.out.println("Angles:"+model.getA11Angle(p));
		System.out.println("Angles:"+model.getA12Angle(p));
		System.out.println("Angles:"+model.getA13Angle(p));
		System.out.println("Angles:"+model.getA14Angle(p));
		System.out.println("Angles:"+model.getA15Angle(p));
		System.out.println("Angles:"+model.getA16Angle(p));
		System.out.println("Angles:"+model.getA17Angle(p));
		System.out.println("Angles:"+model.getA18Angle(p));
		System.out.println("Angles:"+model.getA19Angle(p));
	}
}
