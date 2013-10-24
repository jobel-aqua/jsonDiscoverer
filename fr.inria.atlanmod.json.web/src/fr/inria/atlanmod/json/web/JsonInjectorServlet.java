package fr.inria.atlanmod.json.web;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;

import fr.inria.atlanmod.discoverer.JsonDiscoverer;
import fr.inria.atlanmod.discoverer.JsonInjector;

@WebServlet("/injectModel")
public class JsonInjectorServlet extends AbstractJsonDiscoverer {
	private static final long serialVersionUID = 1L;
	private static final String INJECTOR_ID = "IdInjector";

	/* 
	 * Performs a POST call and returns the a picture in BASE64 representing the model
	 * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String jsonCode = request.getParameter(jsonParam);
		if(jsonCode == null || jsonCode.equals("")) throw new ServletException("No json data in the call");
		
		// 1. Get first the metamodel
		JsonDiscoverer discoverer = new JsonDiscoverer();
		EPackage discoveredMetamodel = discoverer.discoverMetamodel(jsonCode);
		
		// 2. Inject the model
		JsonInjector injector = new JsonInjector(); 
		List<EObject> eObjects = injector.inject(jsonCode, discoveredMetamodel);
		
		// 3. Get the picture
		String id = properties.getProperty(INJECTOR_ID);
		if(id == null) throw new ServletException("ID for injector not found in properties");
		
		File resultPath = drawModel(eObjects, id);		
		String resultImage = encodeToString(resultPath);
		resultPath.delete();
		
		// 4. Write the response
		PrintWriter out = response.getWriter();
        out.print(resultImage);
	}
}
