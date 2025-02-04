import { NoteLocation } from '../../shared/models/note-location';

export interface UploadNoteRequest {
    title: string;
    body: string;
    expiration: string;
    image: string; // TODO: implement this
    location: NoteLocation;
    // no images for now look into https://docs.nestjs.com/techniques/file-upload
    // for documentation on how to do it
}
