import SwiftUI
import shared

struct Tutor : Identifiable {
    let id = UUID()
    let imageUrl = "ic_control_bulb_off"
    let name = "name"
    let headline = "headline"
}

struct ContentView: View {
    var tutors: [Tutor] = [
        Tutor(), Tutor(), Tutor(), Tutor(), Tutor(), Tutor()
    ]
    
    var body: some View {
        List(tutors) { tutor in
            TutorCell(tutor: tutor)
        }.navigationBarTitle(Text("Tutors"))
    }
}

struct ContentView_Previews: PreviewProvider {
	static var previews: some View {
		ContentView()
	}
}


struct TutorCell: View {
    let tutor: Tutor
    var body: some View {
        HStack {
//            AsyncImage(url: URL(string:tutor.imageUrl))
//            { image in
//               image
//                   .resizable()
//                   .scaledToFill()
//           } placeholder: {
//               Color.purple.opacity(0.1)
//           }
//           .frame(width: 60, height: 60)
//           .cornerRadius(20)
            
            Image(tutor.imageUrl)
                .resizable()
                .scaledToFit()
                .frame(width: 32, height: 32)
            
            VStack(alignment: .leading) {
                Text(tutor.name)
                Text(tutor.headline)
                    .font(.subheadline)
            }
        }
    }
}
