import './home-page.scss'
import Header from '../header/header'

export default function HomePage() {
	return (
		<>
			<Header />
			<div className="sw-home-root">
				<div className="sw-home-message">SpendWise Home Page</div>
			</div>
		</>
	)
}
