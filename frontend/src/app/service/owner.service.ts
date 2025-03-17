import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';
import {Owner, OwnerCreate} from '../dto/owner';

const baseUri = environment.backendUrl + '/owners';

@Injectable({
  providedIn: 'root'
})
export class OwnerService {

  constructor(
    private http: HttpClient,
  ) { }

  public searchByName(name: string, limitTo: number): Observable<Owner[]> {
    const params = new HttpParams()
      .set('name', name)
      .set('maxAmount', limitTo);
    return this.http.get<Owner[]>(baseUri, { params });
  }

  /**
   * Create a new owner in the system.
   *
   * @param owner The data for the owner that should be created.
   * @return An Observable for the created owner.
   */
  create(owner: OwnerCreate): Observable<Owner> {
    console.log(owner);
    return this.http.post<Owner>(
      baseUri,
      owner
    );
  }

  /**
   * Retrieve an existing owner from the system by its ID.
   *
   * @param id The unique identifier of the owner to fetch.
   * @return An Observable containing the requested owner details.
   */
  getById(id: number): Observable<Owner> {
    return this.http.get<Owner>(`${baseUri}/${id}`);
  }


  getAll(): Observable<Owner[]> {
    return this.http.get<Owner[]>(baseUri)
  }
}
